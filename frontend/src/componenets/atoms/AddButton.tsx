import {FC, MouseEventHandler} from "react";

export const AddButton: FC<{ onClick?: MouseEventHandler }> = ({onClick}) => {
    return (
        <button onClick={onClick}
                className="text-white bg-blue-500 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full w-6 h-6 flex items-center justify-center text-sm"
        >
            +
        </button>
    )
}
