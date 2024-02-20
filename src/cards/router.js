import Router from 'koa-router';
import itemsStore from './store';
import {broadcast} from "../utils";

export const router = new Router();


router.get('/', async (ctx) => {
    const userId = ctx.state.user._id;

    let items = await itemsStore.find({userId});

    ctx.response.body = items;

    ctx.response.status = 200;
});

router.post('/', async ctx => {
    const userId = ctx.state.user._id;

    const item = ctx.request.body;
    delete item._id
    item.userId = userId;

    ctx.response.body = await itemsStore.insert(item);
    ctx.response.status = 201;
    broadcast(userId, {type: 'created', payload: ctx.response.body});
});

router.put('/:id', async (ctx) => {
    const userId = ctx.state.user._id;

    const item = ctx.request.body;
    item._id = ctx.params.id;
    item.userId = userId;

    await itemsStore.update({_id: ctx.params.id}, item);

    ctx.response.status = 200;
    ctx.response.body = item;

    broadcast(userId, {type: 'updated', payload: ctx.response.body});
});
