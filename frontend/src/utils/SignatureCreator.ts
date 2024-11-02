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


export const generateNewKeys = async (username: string, password: string) => {
    if (localStorage.getItem(username) !== null) {
        console.error("WALLET ALREADY EXISTS")
        return;
    }
    try {
        let [keypair] = await Promise.all([forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001})])
        let privateKey = forge.pki.privateKeyToPem(keypair.privateKey);
        let publicKey = forge.pki.publicKeyToPem(keypair.publicKey);
        let encrypted_key = encryptAES(privateKey, generateIVFromUsername(username), convertToKey(password, 16));
        localStorage.setItem(username, encrypted_key.encryptedData)
        console.log("KEYS GENERATE SUCCESSFULLY")
        return publicKey
    } catch (e) {
        console.log("error", e)

    }
    return "";
}

export const readKeys = (username: string, password: string) => {
    const privateKey = decryptAES(localStorage.getItem(username)!, generateIVFromUsername(username), convertToKey(password, 16));
    console.log(privateKey)
    const pk = forge.pki.privateKeyFromPem(privateKey.trim());

    const publicKey = forge.pki.setRsaPublicKey(pk.n, pk.e);

    const publicKeyPem = forge.pki.publicKeyToPem(publicKey);
    return {
        public: publicKeyPem,
        private: privateKey,
    }
}

const encryptAES = (text: string, iv: string, key: string) => {
    const keyBytes = forge.util.createBuffer(key, 'utf8');

    const cipher = forge.cipher.createCipher('AES-CBC', keyBytes);

    cipher.start({iv: forge.util.createBuffer(iv)});
    cipher.update(forge.util.createBuffer(text, 'utf8'));
    cipher.finish();

    return {
        iv: forge.util.encode64(iv),
        encryptedData: forge.util.encode64(cipher.output.getBytes()),
    };
};

const generateIVFromUsername = (username: string): string => {
    const md = forge.md.sha256.create();
    md.update(username, 'utf8');

    const hash = md.digest().getBytes();
    return convertToKey(hash.slice(0, 16), 16);
};


const decryptAES = (encryptedData: string, iv: string, key: string) => {
    const keyBytes = forge.util.createBuffer(key, 'utf8');

    const decipher = forge.cipher.createDecipher('AES-CBC', keyBytes);
    decipher.start({iv: forge.util.createBuffer(iv)});

    const encryptedBytes = forge.util.decode64(encryptedData);
    decipher.update(forge.util.createBuffer(encryptedBytes));

    const result = decipher.finish();

    if (result) {
        return decipher.output.toString();
    } else {
        throw new Error('Deszyfrowanie nie powiodło się.');
    }
};

const convertToKey = (input: string, length: number): string => {
    const key = input.slice(0, length);
    return key.padEnd(length, '0');
};
