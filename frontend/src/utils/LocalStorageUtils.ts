import {EncryptedWallet} from "../types/EncryptedWallet";
import {decryptAES, encryptAES, privateKeyToPublic} from "./SignatureCreator";
import {WalletDto} from "../types/WalletDto";
import {KeysObject} from "../types/KeysObject";

export const readKeysFromLocalStorage = (user: string, password: string): KeysObject[] => {
    const obj = localStorage.getItem(user)
    if (obj) {
        const encryptedWallet: EncryptedWallet = JSON.parse(obj)
        const wallet: WalletDto = JSON.parse(decryptAES(encryptedWallet.encrypted, encryptedWallet.iv, password))
        return wallet.keys.map(k => {
            return {
                privateKey: k.key,
                publicKey: privateKeyToPublic(k.key),
            }
        })
    }
    throw new Error("COULD_NOT_READ_WALLET")
}

export const createKeysInLocalStorage = (user: string, password: string) => {
    if (localStorage.getItem(user) != null) {
        throw new Error("ACCOUNT_ALREADY_EXISTS")
    }
    const wallet: WalletDto = {
        keys: [],
    };
    const data = encryptAES(JSON.stringify(wallet), password)
    const encryptedWallet: EncryptedWallet = {
        encrypted: data.encryptedData,
        iv: data.iv
    }
    localStorage.setItem(user, JSON.stringify(encryptedWallet))
}

export const addKeyToLocalStorage = (user: string, password: string, privateKey: string) => {
    const obj = localStorage.getItem(user)
    if (obj) {
        const encryptedWallet: EncryptedWallet = JSON.parse(obj)
        const wallet: WalletDto = JSON.parse(decryptAES(encryptedWallet.encrypted, encryptedWallet.iv, password))
        wallet.keys.push({
            key: privateKey,
        })
        const data = encryptAES(JSON.stringify(wallet), password)
        const newEncryptedWallet: EncryptedWallet = {
            encrypted: data.encryptedData,
            iv: data.iv
        }

        localStorage.setItem(user, JSON.stringify(newEncryptedWallet))
        return
    }
    throw new Error("COULD_NOT_UPDATE_WALLET")
}

export const removeKeyFromLocalStorage = (user: string, password: string, privateKey: string) => {

}

