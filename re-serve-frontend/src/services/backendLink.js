import axios from 'axios';

const get = async (url, successCallback, errorCallback) => {
    try {
        const response = await axios.get(
            `http://localhost:8080/reserve/${url}`
        );
        await successCallback(response);
    } catch (error) {
        const errorMessage =
            error.response.data ||
            error.message ||
            'Ops! Something went wrong.';
        await errorCallback(errorMessage);
    }
    return;
};

export default {
    get,
};
