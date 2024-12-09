import {SecuredPage} from "./SecuredPage";
import React, {FC} from "react";
import {CredentialsDto} from "../types/CredentialsDto";
import {KeysObject} from "../types/KeysObject";

export const TransactionPage: FC<{
    credentials?: CredentialsDto,
    keys: KeysObject[],
    setKeys: React.Dispatch<React.SetStateAction<KeysObject[]>>,
}> = ({credentials, keys, setKeys}) => {
    return (
        <SecuredPage credentials={credentials}>

        </SecuredPage>
    )
}
