import { QrCode } from "./qr-code";

export interface AuthenticationResponse {
    jwtTokenWeb: string;
    qrCode: QrCode;
    expirationTime: Date;
}

