import forge from 'node-forge';

export const createBase64Signature = (object: string, rsaPrivateKey: string): string => {
    const privateKey = forge.pki.privateKeyFromPem(rsaPrivateKey);
    const md = forge.md.sha256.create();
    md.update(object, 'utf8');
    const signature = privateKey.sign(md);
    return forge.util.encode64(signature);
};
