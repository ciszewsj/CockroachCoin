import {MainButton} from "../componenets/atoms/MainButton";
import {SecondaryButton} from "../componenets/atoms/SecondaryButton";
import {AccountList} from "../componenets/organisms/AccountList";
import React, {FC, useState} from "react";
import {generateNewKey} from "../utils/SignatureCreator";
import {KeysObject} from "../types/KeysObject";
import {SecuredPage} from "./SecuredPage";
import {CredentialsDto} from "../types/CredentialsDto";
import {addKeyToLocalStorage} from "../utils/LocalStorageUtils";
import ImportFileModal from "../componenets/organisms/ImportFileModal";
import {ChooseHttpAddressButton} from "../componenets/organisms/ChooseHttpAddressButton";
import {useNavigate} from "react-router-dom";

export const WalletPage: FC<{
    credentials?: CredentialsDto,
    keys: KeysObject[],
    setKeys: React.Dispatch<React.SetStateAction<KeysObject[]>>,
}> = ({credentials, keys, setKeys}) => {

    const [importOpen, setImportOpen] = useState<boolean>(false)
    const navigate = useNavigate();

    return (
        <SecuredPage credentials={credentials}>
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                    <ChooseHttpAddressButton/>

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
                        <SecondaryButton onClick={(e) => setImportOpen(true)}>
                            Import
                        </SecondaryButton>
                    </div>
                    <div className="flex space-x-2">
                        <MainButton onClick={() => {
                            navigate("/blocks")
                        }}>
                            Blocks
                        </MainButton>
                        <SecondaryButton onClick={() => {
                            navigate("/transaction")
                        }}>
                            Do transaction
                        </SecondaryButton>
                    </div>
                    <AccountList credentials={credentials!} keys={keys} setKeys={setKeys}/>
                </div>
            </div>
            <ImportFileModal open={importOpen} setOpen={setImportOpen} setKeys={setKeys} keys={keys}/>
        </SecuredPage>
    )
}
