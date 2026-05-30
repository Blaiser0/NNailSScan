"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.completePasswordResetWithOtp = exports.verifyPasswordResetOtp = exports.sendPasswordResetOtp = void 0;
const crypto = __importStar(require("crypto"));
const app_1 = require("firebase-admin/app");
const auth_1 = require("firebase-admin/auth");
const firestore_1 = require("firebase-admin/firestore");
const firebase_functions_1 = require("firebase-functions");
const functions = __importStar(require("firebase-functions/v1"));
const https_1 = require("firebase-functions/v1/https");
const nodemailer = __importStar(require("nodemailer"));
(0, app_1.initializeApp)();
const db = (0, firestore_1.getFirestore)();
const auth = (0, auth_1.getAuth)();
const OTP_COLLECTION = "password_reset_otps";
const OTP_TTL_MS = 10 * 60 * 1000;
const MAX_ATTEMPTS = 5;
const region = functions.region("us-central1");
function normalizeEmail(email) {
    return email.trim().toLowerCase();
}
function getOtpSecret() {
    return process.env.OTP_SECRET || "nailscan-dev-secret-change-in-production";
}
function hashOtp(email, otp) {
    return crypto
        .createHash("sha256")
        .update(`${otp}:${normalizeEmail(email)}:${getOtpSecret()}`)
        .digest("hex");
}
function generateOtp() {
    return crypto.randomInt(100000, 999999).toString();
}
async function sendOtpEmail(email, otp) {
    const smtpHost = process.env.SMTP_HOST;
    const smtpUser = process.env.SMTP_USER;
    const smtpPass = process.env.SMTP_PASS;
    if (!smtpHost || !smtpUser || !smtpPass) {
        firebase_functions_1.logger.warn(`SMTP no configurado. OTP de prueba para ${email}: ${otp}`);
        return;
    }
    const transporter = nodemailer.createTransport({
        host: smtpHost,
        port: Number(process.env.SMTP_PORT || 587),
        secure: false,
        auth: {
            user: smtpUser,
            pass: smtpPass,
        },
    });
    await transporter.sendMail({
        from: `"NailScan" <${smtpUser}>`,
        to: email,
        subject: "Código de recuperación NailScan",
        text: `Tu código de recuperación es: ${otp}. Válido por 10 minutos.`,
        html: `<p>Tu código de recuperación es: <strong>${otp}</strong></p><p>Válido por 10 minutos.</p>`,
    });
}
async function verifyOtpInternal(email, code, markVerified) {
    const normalized = normalizeEmail(email);
    if (!/^\d{6}$/.test(code)) {
        throw new https_1.HttpsError("invalid-argument", "El código debe tener 6 dígitos.");
    }
    const docRef = db.collection(OTP_COLLECTION).doc(normalized);
    const snap = await docRef.get();
    if (!snap.exists) {
        throw new https_1.HttpsError("not-found", "Código inválido o expirado.");
    }
    const data = snap.data();
    if (data.attempts >= MAX_ATTEMPTS) {
        throw new https_1.HttpsError("resource-exhausted", "Demasiados intentos. Solicita un código nuevo.");
    }
    if (data.expiresAt.toMillis() < Date.now()) {
        await docRef.delete();
        throw new https_1.HttpsError("deadline-exceeded", "El código ha expirado. Solicita uno nuevo.");
    }
    if (hashOtp(normalized, code) !== data.otpHash) {
        await docRef.update({ attempts: firestore_1.FieldValue.increment(1) });
        throw new https_1.HttpsError("invalid-argument", "Código inválido. Verifica e inténtalo de nuevo.");
    }
    if (markVerified) {
        await docRef.update({ verified: true });
    }
    return normalized;
}
exports.sendPasswordResetOtp = region.https.onCall(async (data) => {
    const email = data?.email;
    if (!email || typeof email !== "string") {
        throw new https_1.HttpsError("invalid-argument", "Correo electrónico no válido.");
    }
    const normalized = normalizeEmail(email);
    try {
        await auth.getUserByEmail(normalized);
    }
    catch {
        return { success: true };
    }
    const otp = generateOtp();
    await db.collection(OTP_COLLECTION).doc(normalized).set({
        otpHash: hashOtp(normalized, otp),
        expiresAt: firestore_1.Timestamp.fromMillis(Date.now() + OTP_TTL_MS),
        attempts: 0,
        verified: false,
        createdAt: firestore_1.FieldValue.serverTimestamp(),
    });
    await sendOtpEmail(normalized, otp);
    return { success: true };
});
exports.verifyPasswordResetOtp = region.https.onCall(async (data) => {
    const email = data?.email;
    const code = data?.code;
    if (!email || typeof email !== "string" || !code || typeof code !== "string") {
        throw new https_1.HttpsError("invalid-argument", "Correo y código son obligatorios.");
    }
    await verifyOtpInternal(email, code, true);
    return { success: true };
});
exports.completePasswordResetWithOtp = region.https.onCall(async (data) => {
    const email = data?.email;
    const code = data?.code;
    const newPassword = data?.newPassword;
    if (!email || !code || !newPassword) {
        throw new https_1.HttpsError("invalid-argument", "Datos incompletos.");
    }
    if (typeof newPassword !== "string" || newPassword.length < 6) {
        throw new https_1.HttpsError("invalid-argument", "La contraseña es demasiado débil (mínimo 6 caracteres).");
    }
    const normalized = await verifyOtpInternal(email, code, false);
    const user = await auth.getUserByEmail(normalized);
    await auth.updateUser(user.uid, { password: newPassword });
    await db.collection(OTP_COLLECTION).doc(normalized).delete();
    return { success: true };
});
//# sourceMappingURL=index.js.map