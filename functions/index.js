/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const {CloudTasksClient} = require('@google-cloud/tasks');
const client = new CloudTasksClient();
admin.initializeApp();

exports.manageReservationNotification = functions.firestore
    .document('schedule/{key}')
    .onWrite(async (change, context) => {
        // 문서가 삭제된 경우
        if (!change.after.exists) {
            // 삭제된 문서에 대한 처리 로직
            console.log('Document deleted, cancel any scheduled notification.');
            // 여기에 알림 취소 로직을 추가합니다.
            return;
        }

        const notification = change.after.data();

        if (!notification || !notification.notificationTime) return

        const projectId = 'colink-a7c3a';
        const queueName = 'YOUR_QUEUE_NAME';
        const location = 'YOUR_QUEUE_LOCATION';
        const url = 'YOUR_CLOUD_FUNCTION_TRIGGER_URL';
        const payload = { key: context.params.key }; // 예를 들어, 처리할 작업의 key

        const scheduleTime = new Date(notification.notificationTime).getTime();
        const now = Date.now();

        if (scheduleTime <= now) {
            console.log('The scheduled time is in the past. Ignoring.');
            return;
        }


        const task = {
            httpRequest: {
                httpMethod: 'POST',
                url,
                body: Buffer.from(JSON.stringify(payload)).toString('base64'),
                headers: {
                    'Content-Type': 'application/json',
                },
            },
            scheduleTime: {
                seconds: scheduleTime / 1000
            }
        };

        const request = {
            parent: client.queuePath(projectId, location, queueName),
            task,
        };

        try {
            const [response] = await client.createTask(request);
            console.log(`Task ${response.name} created.`);
        } catch (error) {
            console.error(`Error creating task: ${error}`);
        }
    });


    // .onCreate(async (snapshot, context) => {
    //     const notificationData = snapshot.data();

    //     if (notificationData.type && notificationData.type === 'RESERVATION') {
    //         // FCM 메시지 구성
    //         const message = {
    //             notification: {
    //                 title: notificationData.title || '새 예약 알림',
    //                 body: notificationData.message || ''
    //             },
    //             token: notificationData.toUserToken
    //         };

    //         // FCM 알림 전송 시도
    //         try {
    //             const response = await admin.messaging().send(message);
    //             console.log('Successfully sent message:', response);
    //         } catch (error) {
    //             console.log('Error sending message:', error);
    //         }
    //     } else {
    //         console.log('Notification type is not reservation. No action taken.');
    //     }
    // });