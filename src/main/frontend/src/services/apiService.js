import axios from "axios";

let authToken = 'Basic nothing';

export default {

    createClient() {
        const headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': authToken,
            'Access-Control-Allow-Origin': '*'
        };

        return axios.create({
            headers: headers,
            withCredentials: false
        });
    },
    logIn(payload) {

        let instance = this.createClient();

        return instance.post(
            process.env.VUE_APP_API_BASE_URL + 'auth/login',
            payload
        ).then(response => {
            authToken = "Bearer " + response.data.token;
            return true;
        })
        .catch(error => {
            console.log(error);
            return false;
        });
    },
    getQuestions(payload) {
        let instance = this.createClient();

        return instance.post(
            process.env.VUE_APP_API_BASE_URL + 'api/get-book-chapter-questions',
            payload
        );
    },
    checkAnswers(requestAnswers) {

        let instance = this.createClient();

        return instance
            .post(
                process.env.VUE_APP_API_BASE_URL + 'api/check-answers',
                requestAnswers
            )
    }
};