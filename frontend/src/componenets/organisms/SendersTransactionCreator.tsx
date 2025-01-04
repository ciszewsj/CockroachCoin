import {AddButton} from "../atoms/AddButton";
import {RemoveButton} from "../atoms/RemoveButton";
import React, {FC, useState, useEffect, Dispatch, SetStateAction} from "react";
import {SenderCreatorDto} from "../../types/SenderCreatorDto";
import {KeysObject} from "../../types/KeysObject";
import {cleanKey} from "../../utils/ClearKey";

export const SendersTransactionCreator: FC<{
    keys: KeysObject[],
    senders: SenderCreatorDto[],
    setSenders: Dispatch<SetStateAction<SenderCreatorDto[]>>
}> = ({keys, setSenders, senders}) => {

    useEffect(() => {
        if (keys.length > 0) {
            setSenders([
                {
                    key: {
                        publicKey: keys[0].publicKey,
                        privateKey: keys[0].privateKey,
                    },
                    amount: 0,
                },
            ]);
        }
    }, [keys]);

    return (
        <>
            <h3 className="text-xl flex items-center space-x-2">
                <AddButton
                    onClick={() => {
                        setSenders([
                            ...senders,
                            {
                                key: {
                                    publicKey: keys[0]?.publicKey || "",
                                    privateKey: keys[0]?.privateKey || "",
                                },
                                amount: 0,
                            },
                        ]);
                    }}
                />
                From:
            </h3>

            {senders.map((sender, id) => (
                <label key={id} className="text-sm font-medium text-gray-700 flex items-center space-x-2">
                    <RemoveButton
                        onClick={() => {
                            setSenders(senders.filter((_, i) => i !== id));
                        }}
                    />
                    <div className="flex space-x-2 items-center">
                        <select
                            className="border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 px-3 py-1 text-sm w-32"
                            value={keys.findIndex((key) => key.publicKey === sender.key.publicKey)}
                            onChange={(e) => {
                                const selectedIndex = Number(e.target.value);
                                const selectedKey = keys[selectedIndex];
                                const updatedSenders = [...senders];
                                updatedSenders[id] = {
                                    ...sender,
                                    key: selectedKey
                                        ? {
                                            publicKey: selectedKey.publicKey,
                                            privateKey: selectedKey.privateKey,
                                        }
                                        : {publicKey: "", privateKey: ""},
                                };
                                setSenders(updatedSenders);
                            }}
                            style={{
                                maxWidth: '200px',
                                overflow: 'hidden',
                            }}
                        >
                            {keys.map((key, idx) => (
                                <option
                                    key={idx}
                                    value={idx}
                                    title={key.publicKey}
                                    className="truncate text-xl"
                                    style={{
                                        maxWidth: '200px',
                                        overflow: 'hidden',
                                        whiteSpace: 'nowrap',
                                        textOverflow: 'ellipsis',
                                    }}
                                >
                                    {`${idx}_`} {cleanKey(key.publicKey).substring(0, 15)}
                                </option>
                            ))}
                        </select>

                        <input
                            type="number"
                            placeholder="0"
                            className="border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 px-3 py-1 text-sm w-24"
                            value={sender.amount}
                            onChange={(e) => {
                                const updatedSenders = [...senders];
                                updatedSenders[id] = {...sender, amount: Number(e.target.value)};
                                setSenders(updatedSenders);
                            }}
                        />
                    </div>
                </label>
            ))}
        </>
    );
};
