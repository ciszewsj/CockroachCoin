import {AccountField} from "../molecules/AccountField";
import {FC, useEffect, useState} from "react";
import {AccountDetails} from "../../types/AccountDetails";
import {KeysObject} from "../../types/KeysObject";

export const AccountList: FC<{ keys: KeysObject[] }> = ({keys}) => {

    const [accounts, setAccounts] = useState<AccountDetails[]>([])

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
                accounts.map(((account, index) => <AccountField key={index} accounts={account}/>))
            }
        </div>
    )
}
