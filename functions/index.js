const functions = require("firebase-functions");
const admin = require("firebase-admin");
const serviceAccount = require("./admin.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const request = require("request-promise");

const kakaoRequestMeUrl = "https://kapi.kakao.com/v2/user/me";

/**
 * Requests user profile from Kakao API server.
 * @param {string} kakaoAccessToken The access token obtained from Kakao login.
 * @return {Promise<any>} The promise containing the request result.
 */
function requestMe(kakaoAccessToken) {
  console.log("Requesting user profile from Kakao API server.");
  return request({
    method: "GET",
    headers: {Authorization: `Bearer ${kakaoAccessToken}`},
    url: kakaoRequestMeUrl,
  });
}

/**
 * Updates or creates a Firebase user.
 * @param {string} userId The user ID.
 * @param {string} email The user's email address.
 * @param {string} displayName The user's display name.
 * @param {string} photoURL The URL of the user's profile photo.
 * @return {Promise<any>} The promise containing the update or creation result.
 */
function updateOrCreateUser(userId, email, displayName, photoURL) {
  console.log("Updating or creating a Firebase user");
  const updateParams = {
    provider: "KAKAO",
    displayName: displayName || email,
    email: email,
  };
  if (photoURL) {
    updateParams.photoURL = photoURL;
  }
  console.log(updateParams);
  return admin
      .auth()
      .updateUser(userId, updateParams)
      .catch((err) => {
        if (err.code === "auth/user-not-found") {
          updateParams.uid = userId;
          if (email) {
            updateParams.email = email;
          }
          return admin.auth().createUser(updateParams);
        }
        throw err;
      });
}

/**
 * Creates a Firebase token based on the provided Kakao access token.
 * @param {string} kakaoAccessToken The Kakao access token.
 * @return {Promise<string>} A promise that resolves with the Firebase token.
 */
function createFirebaseToken(kakaoAccessToken) {
  return requestMe(kakaoAccessToken).then((response) => {
    const body = JSON.parse(response);
    console.log(body);
    const userId = `kakao:${body.id}`;
    if (!userId) {
      throw new functions.https.HttpsError(
          "invalid-argument",
          "Not response: Failed get userId",
      );
    }

    let nickname = null;
    let profileImage = null;
    let email = null;
    if (body.properties) {
      nickname = body.properties.nickname;
      profileImage = body.properties.profile_image;
    }
    if (body.kakao_account) {
      email = body.kakao_account.email;
    }
    return updateOrCreateUser(userId, email, nickname, profileImage);
  }).then((userRecord) => {
    const userId = userRecord.uid;
    console.log(`creating a custom firebase token based on uid ${userId}`);
    return admin.auth().createCustomToken(userId, {provider: "KAKAO"});
  });
}

exports.kakaoCustomAuth = functions.region("asia-northeast3").https.onCall(
    (data) => {
      const token = data.token;

      if (!(typeof token === "string") || token.length === 0) {
        throw new functions.https.HttpsError(
            "invalid-argument",
            "The function must be called with one argument 'data' containing " +
            "the token to add.",
        );
      }

      console.log(`Verifying Kakao token: ${token}`);

      return createFirebaseToken(token).then((firebaseToken) => {
        console.log(`Returning firebase token to user: ${firebaseToken}`);
        return {custom_token: firebaseToken};
      });
    },
);
