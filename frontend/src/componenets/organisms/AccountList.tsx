import {AccountField} from "../molecules/AccountField";
import React, {FC, useCallback, useEffect, useState} from "react";
import {AccountDetails} from "../../types/AccountDetails";
import {KeysObject} from "../../types/KeysObject";
import {removeKeyFromLocalStorage} from "../../utils/LocalStorageUtils";
import {CredentialsDto} from "../../types/CredentialsDto";

export const AccountList: FC<{
    credentials: CredentialsDto,
    keys: KeysObject[],
    setKeys: React.Dispatch<React.SetStateAction<KeysObject[]>>
}> = ({credentials, keys, setKeys}) => {

    const [accounts, setAccounts] = useState<AccountDetails[]>([])

    const deletePrivateKey = useCallback((p: string) => {
        removeKeyFromLocalStorage(credentials.username, credentials.password, p);
        setKeys(keys.filter(k => k.privateKey !== p));
    }, [credentials.username, credentials.password, setKeys, keys]);

    useEffect(() => {
        setAccounts(keys.map(key => {
            return {
                privateKey: key.privateKey,
                publicKey: key.publicKey,
            }
        }))
    }, [keys]);

    return (
        <div className="space-y-2">
            {
                accounts.map(((account, index) => <AccountField key={index} accounts={account}
                                                                handleDelete={deletePrivateKey}/>))
            }
        </div>
    )
}
