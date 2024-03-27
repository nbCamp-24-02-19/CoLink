const functions = require('firebase-functions')
const admin = require('firebase-admin')
const serviceAccount = require("./admin.json")
const { auth } = require('firebase-admin')
const { error } = require('firebase-functions/logger')

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore()

const request = require('request-promise')

const kakaoRequestMeUrl = 'https://kapi.kakao.com/v2/user/me'

function requestMe(kakaoAccessToken) {
    console.log('Requesting user profile from Kakao API server.')
    return request({
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + kakaoAccessToken },
        url: kakaoRequestMeUrl,
    })
}

function updateOrCreateUser(userId, email, displayName, photoURL) {
    console.log('updating or creating a firebase user');
    const updateParams = {
        provider: 'KAKAO',
        displayName: displayName,
        email: email,
    };
    if (displayName) {
        updateParams['displayName'] = displayName;
    } else {
        updateParams['displayName'] = email;
    }
    if (photoURL) {
        updateParams['photoURL'] = photoURL;
    }
    console.log(updateParams);
    return admin.auth().updateUser(userId, updateParams)
        .catch((error) => {
            if (error.code === 'auth/user-not-found') {
                updateParams['uid'] = userId;
                if (email) {
                    updateParams['email'] = email;
                }
                return admin.auth().createUser(updateParams);
            }
            throw error;
        });
}

function createFirebaseToken(kakaoAccessToken) {
    return requestMe(kakaoAccessToken).then((response) => {
        const body = JSON.parse(response)
        console.log(body)
        const userId = `kakao:${body.id}`
        if (!userId) {
            throw new functions.https.HttpsError('invalid-argument', 'Not response: Failed get userId');
        }

        let nickname = null
        let profileImage = null
        let email = null
        if (body.properties) {
            nickname = body.properties.nickname
            profileImage = body.properties.profile_image
        }
        if (body.kakao_account) {
            email = body.kakao_account.email
        }
        return updateOrCreateUser(userId, email, nickname,
            profileImage)
    }).then((userRecord) => {
        const userId = userRecord.uid
        console.log(`creating a custom firebase token based on uid ${userId}`)
        return admin.auth().createCustomToken(userId, { provider: 'KAKAO' })
    })
}

exports.kakaoCustomAuth = functions.region('asia-northeast3').https
    .onCall((data) => {
        const token = data.token

        if (!(typeof token === 'string') || token.length === 0) {
            throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
                'one arguments "data" containing the token to add.');
        }

        console.log(`Verifying Kakao token: ${token}`)

        return createFirebaseToken(token).then((firebaseToken) => {
            console.log(`Returning firebase token to user: ${firebaseToken}`)
            return { "custom_token": firebaseToken };
        })

    })