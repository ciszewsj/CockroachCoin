import React, {useContext, useState} from 'react';
import {MainButton} from "../atoms/MainButton";
import {HttpAddressContext} from "../../context/HttpAddressProvider";

export const ChooseHttpAddressButton = () => {
    const [httpAddress, setHttpAddress] = useContext(HttpAddressContext)!!;
    const [customAddress, setCustomAddress] = useState(httpAddress);

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const inputAddress = event.target.value;
        setCustomAddress(inputAddress);
    };

    const handleUpdateClick = () => {
        setHttpAddress(customAddress);
    };

    return (
        <>
            <p className="text-gray-700 text-lg font-medium">
                Current HTTP Address: <span className="font-semibold">{httpAddress}</span>
            </p>
            <div className="flex border border-gray-300 rounded-md overflow-hidden">
                <input
                    type="text"
                    value={customAddress}
                    onChange={handleInputChange}
                    className="w-2/3 px-4 py-2 text-gray-700 focus:outline-none"
                    placeholder="https://custom-url.com"
                />
            </div>

            <MainButton onClick={handleUpdateClick}>
                Update Address
            </MainButton>
        </>
    );
};
