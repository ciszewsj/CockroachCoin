import {useState} from "react";
import {TextInputField} from "../componenets/atoms/TextInputField";
import {MainButton} from "../componenets/atoms/MainButton";

export const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (e: any) => {
        e.preventDefault();
        console.log('Email:', email);
        console.log('Password:', password);
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                <h2 className="text-2xl font-bold text-center text-gray-800">Login</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <TextInputField value={email} onChange={(e) => setEmail(e.target.value)}
                                    type={"email"}>
                        Email
                    </TextInputField>
                    <TextInputField value={password} onChange={(e) => setPassword(e.target.value)}
                                    type={"password"}>
                        Password
                    </TextInputField>

                    <MainButton>
                        Log in
                    </MainButton>
                </form>
            </div>
        </div>
    )
}
