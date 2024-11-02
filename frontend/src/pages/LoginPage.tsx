import {useState} from "react";
import {TextInputField} from "../componenets/atoms/TextInputField";
import {MainButton} from "../componenets/atoms/MainButton";
import {useNavigate} from "react-router-dom";
import {SecondaryButton} from "../componenets/atoms/SecondaryButton";
import {createKeysInLocalStorage, readKeysFromLocalStorage} from "../utils/LocalStorageUtils";

export const LoginPage = () => {
    const navigate = useNavigate();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = (e: any) => {
        e.preventDefault();
        try {
            readKeysFromLocalStorage(username, password)
            navigate('/wallet');
        } catch (err) {
            console.error(err)
        }
    };

    const handleCreateWallet = (e: any) => {
        e.preventDefault()
        try {
            createKeysInLocalStorage(username, password)
        } catch (err) {
            console.error(err)
        }
    }

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                <h2 className="text-2xl font-bold text-center text-gray-800">Login</h2>
                <form onSubmit={handleLogin} className="space-y-4">
                    <TextInputField value={username} onChange={(e) => setUsername(e.target.value)}
                                    type={"text"}>
                        Username
                    </TextInputField>
                    <TextInputField value={password} onChange={(e) => setPassword(e.target.value)}
                                    type={"password"}>
                        Password
                    </TextInputField>
                    <div className="flex space-x-2">

                        <MainButton>
                            Log in
                        </MainButton>
                        <SecondaryButton onClick={handleCreateWallet}>
                            Crete wallet
                        </SecondaryButton>
                    </div>
                </form>
            </div>
        </div>
    )
}
