import dataStore from 'nedb-promise';

export class UserStore {
    constructor({filename, autoload}) {
        this.store = dataStore({filename, autoload});
    }

    async findOne(props) {
        return this.store.findOne(props);
    }
}

export default new UserStore({filename: './db/users.json', autoload: true});