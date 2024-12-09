import {AddButton} from "../atoms/AddButton";
import {RemoveButton} from "../atoms/RemoveButton";
import React, {useState} from "react";
import {ReceiverCreatorDto} from "../../types/ReceiverCreatorDto";

export const ReceiversTransactionCreator = () => {
    const [recivers, setRecivers] = useState<ReceiverCreatorDto[]>([])
    return (
        <>
            <h3 className="text-xl flex items-center space-x-2">
                <AddButton
                    onClick={() => {
                        setRecivers([
                            ...recivers,
                            {
                                key: "",
                                amount: 0,
                            },
                        ]);
                    }}
                />
                To:
            </h3>

            {recivers.map((receiver, id) => (
                <label key={id} className="text-sm font-medium text-gray-700 flex items-center space-x-2">
                    <RemoveButton
                        onClick={() => {
                            setRecivers(recivers.filter((_, i) => i !== id));
                        }}
                    />
                    <div className="flex space-x-2 items-center">
                        <input
                            type="text"
                            placeholder="Enter text"
                            className="border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 px-3 py-1 text-sm"
                            value={receiver.key}
                            onChange={(e) => {
                                const updatedReceivers = [...recivers];
                                updatedReceivers[id] = {...receiver, key: e.target.value};
                                setRecivers(updatedReceivers);
                            }}
                        />
                        <input
                            type="number"
                            placeholder="0"
                            className="border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 px-3 py-1 text-sm w-24"
                            value={receiver.amount}
                            onChange={(e) => {
                                const updatedReceivers = [...recivers];
                                updatedReceivers[id] = {...receiver, amount: Number(e.target.value)};
                                setRecivers(updatedReceivers);
                            }}
                        />
                    </div>
                </label>
            ))}
        </>
    )
}
