import {MainButton} from "../componenets/atoms/MainButton";
import {SecondaryButton} from "../componenets/atoms/SecondaryButton";
import {AccountList} from "../componenets/organisms/AccountList";
import {FC, useEffect, useState} from "react";
import {WalletDto} from "../types/WalletDto";
import {generateNewKey} from "../utils/SignatureCreator";
import {KeysObject} from "../types/KeysObject";
import {SecuredPage} from "./SecuredPage";
import {CredentialsDto} from "../types/CredentialsDto";
import {addKeyToLocalStorage} from "../utils/LocalStorageUtils";

export const WalletPage: FC<{
    credentials?: CredentialsDto,
    keys: KeysObject[],
    setKeys: any,
}> = ({credentials, keys, setKeys}) => {


    let readWallet = () => {
        console.log(localStorage.getItem("user"))
    }


    useEffect(() => {
        const dto: WalletDto = {
            keys: []
        }

        readWallet()
    }, []);

    return (
        <SecuredPage credentials={credentials}>
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                    <h2 className="text-2xl font-bold text-center text-gray-800">Wallet</h2>

                    <div className="flex space-x-2">
                        <MainButton
                            onClick={async () => {
                                const newKeys: KeysObject = await generateNewKey()
                                addKeyToLocalStorage(credentials!.username, credentials!.password, newKeys.privateKey)
                                setKeys([
                                    ...keys,
                                    newKeys,
                                ])
                            }}
                        >
                            Generate
                        </MainButton>
                        <SecondaryButton>
                            Import
                        </SecondaryButton>
                    </div>
                    <AccountList keys={keys}/>
                </div>
            </div>
        </SecuredPage>
    )
}
