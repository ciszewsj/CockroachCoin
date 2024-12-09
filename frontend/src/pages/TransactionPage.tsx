import {SecuredPage} from "./SecuredPage";
import React, {FC} from "react";
import {CredentialsDto} from "../types/CredentialsDto";
import {KeysObject} from "../types/KeysObject";
import {MainButton} from "../componenets/atoms/MainButton";
import {useNavigate} from "react-router-dom";
import {SecondaryButton} from "../componenets/atoms/SecondaryButton";
import {AddButton} from "../componenets/atoms/AddButton";
import {RemoveButton} from "../componenets/atoms/RemoveButton";
import {ReceiversTransactionCreator} from "../componenets/organisms/ReceiversTransactionCreator";
import {SendersTransactionCreator} from "../componenets/organisms/SendersTransactionCreator";

export const TransactionPage: FC<{
    credentials?: CredentialsDto,
    keys: KeysObject[],
    setKeys: React.Dispatch<React.SetStateAction<KeysObject[]>>,
}> = ({credentials, keys, setKeys}) => {
    const navigate = useNavigate()
    return (
        <SecuredPage credentials={credentials}>
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                    <h2 className="text-2xl font-bold text-center text-gray-800">Do transaction</h2>
                    <SecondaryButton onClick={() => {
                        navigate("/wallet")
                    }}>
                        Wallet
                    </SecondaryButton>
                    <SendersTransactionCreator keys={keys}/>
                    <ReceiversTransactionCreator/>
                    <MainButton onClick={() => {
                        navigate("/wallet")
                    }}>
                        Do tranasaction
                    </MainButton>
                </div>
            </div>
        </SecuredPage>
    )
}
