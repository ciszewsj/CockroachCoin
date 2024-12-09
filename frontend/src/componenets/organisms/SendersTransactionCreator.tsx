import {AddButton} from "../atoms/AddButton";
import {RemoveButton} from "../atoms/RemoveButton";
import React from "react";

export const SendersTransactionCreator = () => {

    return (
        <>
            <h3 className="text-xl flex items-center space-x-2">
                <AddButton/>From:
            </h3>
            <label className="text-sm font-medium text-gray-700 flex items-center space-x-2">
                <RemoveButton/>
                <div className="flex space-x-2 items-center">
                    <select
                        className="border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 px-3 py-1 text-sm"
                    >
                        <option value="" disabled selected>
                            Select an option
                        </option>
                        <option value="option1">Option 1</option>
                        <option value="option2">Option 2</option>
                        <option value="option3">Option 3</option>
                    </select>
                    <input
                        type="number"
                        placeholder="0"
                        className="border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 px-3 py-1 text-sm w-24"
                    />
                </div>
            </label>
        </>
    )
}
