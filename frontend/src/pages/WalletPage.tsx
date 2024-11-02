import {MainButton} from "../componenets/atoms/MainButton";
import {SecondaryButton} from "../componenets/atoms/SecondaryButton";
import {AccountList} from "../componenets/organisms/AccountList";
import {useEffect, useState} from "react";
import {WalletDto} from "../types/WalletDto";
import {generateNewKey} from "../utils/SignatureCreator";
import {KeysObject} from "../types/KeysObject";

export const WalletPage = () => {

    const [wallet, setWallet] = useState<WalletDto>()
    const [keys, setKeys] = useState<KeysObject[]>([])

    let readWallet = () => {
        console.log(localStorage.getItem("user"))
    }


    useEffect(() => {
        const dto: WalletDto = {
            keys: []
        }
        setWallet(dto)

        readWallet()
    }, []);

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                <h2 className="text-2xl font-bold text-center text-gray-800">Wallet</h2>

                <div className="flex space-x-2">
                    <MainButton
                        onClick={async () => {
                            const newKeys: KeysObject = await generateNewKey()
                            // const oldKeys = wallet ? wallet.keys : []
                            console.log(newKeys)
                            setKeys([
                                ...keys,
                                newKeys,
                            ])

                            // setWallet(
                            //     {
                            //         ...wallet,
                            //         keys: [...oldKeys, {
                            //             key: newKeys.privateKey
                            //         }]
                            //     }
                            // )
                        }}
                    >
                        Generate
                    </MainButton>
                    <SecondaryButton>
                        Import
                    </SecondaryButton>
                </div>
                {wallet && <AccountList keys={keys}/>}
            </div>
        </div>


    )
}
