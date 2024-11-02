import {ChangeEventHandler, FC, HTMLInputTypeAttribute} from "react";

export const TextInputField: FC<{
    children: string,
    value?: string | readonly string[] | number | undefined;
    onChange?: ChangeEventHandler<any> | undefined;
    type?: HTMLInputTypeAttribute | undefined;
}> = ({children, value, onChange, type}) => {
    return (
        <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                {children}
            </label>
            <input
                type={type}
                id="email"
                value={value}
                onChange={onChange}
                required
                className="w-full px-3 py-2 mt-1 border border-gray-300 rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
        </div>
    )
}
