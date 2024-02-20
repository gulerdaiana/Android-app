import dataStore from 'nedb-promise';

export class ItemsStore {
    constructor({filename, autoload}) {
        this.store = dataStore({filename, autoload});
    }

    async find(props) {
        return this.store.find(props);
    }

    async insert(item) {
        return this.store.insert(item);
    };

    async update(props, item) {
        return this.store.update(props, item);
    }
}

export default new ItemsStore({filename: './db/items.json', autoload: true});