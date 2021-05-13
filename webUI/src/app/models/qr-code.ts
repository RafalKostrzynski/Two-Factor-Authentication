  export interface QrCode {
        purpose: string;
        jwtToken: string;
        expirationTime: Date;
        payload: string;
    }