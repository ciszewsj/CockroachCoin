import React from 'react';
import {LoginPage} from "./pages/LoginPage";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {WalletPage} from "./pages/WalletPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path={"/"} element={<LoginPage/>}/>
                <Route path={"/wallet"} element={<WalletPage/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
