import forge from 'node-forge';

export const createBase64Signature = (object: string, rsaPrivateKey: string): string => {
    console.log(rsaPrivateKey)
    const privateKey = forge.pki.privateKeyFromPem(rsaPrivateKey);
    const md = forge.md.sha256.create();
    md.update(object, 'utf8');
    const signature = privateKey.sign(md);
    return forge.util.encode64(signature);
};

export const generateNewKey = async () => {
    let [keypair] = await Promise.all([forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001})])
    let privateKey = forge.pki.privateKeyToPem(keypair.privateKey);
    let publicKey = forge.pki.publicKeyToPem(keypair.publicKey);
    return {
        publicKey: publicKey,
        privateKey: privateKey,
    }
}

export const privateKeyToPublic = (privateKey: string) => {
    const pk = forge.pki.privateKeyFromPem(privateKey.trim());
    const publicKey = forge.pki.setRsaPublicKey(pk.n, pk.e);
    return forge.pki.publicKeyToPem(publicKey);
}


export const encryptAES = (text: string, password: string) => {
    const keyBytes = forge.util.createBuffer(convertToKey(password, 16), 'utf8');
    const iv = forge.random.getBytesSync(16);
    const cipher = forge.cipher.createCipher('AES-CBC', keyBytes);
    cipher.start({iv: forge.util.createBuffer(iv)});
    cipher.update(forge.util.createBuffer(text, 'utf8'));
    cipher.finish();

    return {
        iv: forge.util.encode64(iv),
        encryptedData: forge.util.encode64(cipher.output.getBytes()),
    };
};

export const decryptAES = (encryptedData: string, iv: string, password: string) => {
    const keyBytes = forge.util.createBuffer(convertToKey(password, 16), 'utf8');

    if (iv.length !== 24) {
        throw new Error('NOT_VALID_IV');
    }

    const decipher = forge.cipher.createDecipher('AES-CBC', keyBytes);
    decipher.start({iv: forge.util.createBuffer(forge.util.decode64(iv))});

    const encryptedBytes = forge.util.decode64(encryptedData);
    decipher.update(forge.util.createBuffer(encryptedBytes));

    const result = decipher.finish();

    if (result) {
        return decipher.output.toString();
    } else {
        throw new Error('DECRYPT_FAIL');
    }
};


const convertToKey = (input: string, length: number): string => {
    const key = input.slice(0, length);
    return key.padEnd(length, '0');
};
