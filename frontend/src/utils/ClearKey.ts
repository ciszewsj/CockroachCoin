export const cleanKey = (key: string) => {
    return key.replace(/-----BEGIN PUBLIC KEY-----/g, '')
        .replace(/-----END PUBLIC KEY-----/g, '')
        .replace(/\s+/g, '')
}
