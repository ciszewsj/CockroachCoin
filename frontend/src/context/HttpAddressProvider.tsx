import {createContext, FC, useState} from "react";

export const HttpAddressContext = createContext<[string, (newAddress: string) => void] | undefined>(undefined);

export const HttpAddressProvider: FC<{ children: any }> = ({children}) => {
    const [httpAddress, setHttpAddress] = useState<string>('http://localhost:8080');

    const updateAddress = (newAddress: string) => setHttpAddress(newAddress);

    return (
        <HttpAddressContext.Provider value={[httpAddress, updateAddress]}>
            {children}
        </HttpAddressContext.Provider>
    );
};
