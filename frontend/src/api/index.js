import api from './client';

export const storeApi = {
  getAll:        ()           => api.get('/stores').then(r => r.data),
  getById:       (id)         => api.get(`/stores/${id}`).then(r => r.data),
  create:        (data)       => api.post('/stores', data).then(r => r.data),
  update:        (id, data)   => api.put(`/stores/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/stores/${id}`),
  activate:      (id)         => api.patch(`/stores/${id}/activate`).then(r => r.data),
  deactivate:    (id)         => api.patch(`/stores/${id}/deactivate`).then(r => r.data),
  count:         ()           => api.get('/stores/count').then(r => r.data),
  search:        (name)       => api.get('/stores/search', { params: { name } }).then(r => r.data),
};

export const itemApi = {
  getAll:        ()           => api.get('/items').then(r => r.data),
  getById:       (id)         => api.get(`/items/${id}`).then(r => r.data),
  create:        (data)       => api.post('/items', data).then(r => r.data),
  update:        (id, data)   => api.put(`/items/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/items/${id}`),
  count:         ()           => api.get('/items/count').then(r => r.data),
  search:        (keyword)    => api.get('/items/search', { params: { keyword } }).then(r => r.data),
  getByStore:    (storeId)    => api.get(`/items/store/${storeId}`).then(r => r.data),
  getActive:     ()           => api.get('/items/active').then(r => r.data),
};

export const personApi = {
  getAll:        ()           => api.get('/persons').then(r => r.data),
  getById:       (id)         => api.get(`/persons/${id}`).then(r => r.data),
  create:        (data)       => api.post('/persons', data).then(r => r.data),
  update:        (id, data)   => api.put(`/persons/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/persons/${id}`),
  count:         ()           => api.get('/persons/count').then(r => r.data),
  search:        (name)       => api.get('/persons/search', { params: { name } }).then(r => r.data),
};

export const cashierApi = {
  getAll:        ()           => api.get('/cashiers').then(r => r.data),
  getById:       (id)         => api.get(`/cashiers/${id}`).then(r => r.data),
  create:        (data)       => api.post('/cashiers', data).then(r => r.data),
  update:        (id, data)   => api.put(`/cashiers/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/cashiers/${id}`),
  terminate:     (id)         => api.patch(`/cashiers/${id}/terminate`).then(r => r.data),
  count:         ()           => api.get('/cashiers/count').then(r => r.data),
  getByStore:    (storeId)    => api.get(`/cashiers/store/${storeId}`).then(r => r.data),
  getActive:     ()           => api.get('/cashiers/active').then(r => r.data),
};

export const taxCategoryApi = {
  getAll:        ()           => api.get('/tax-categories').then(r => r.data),
  getById:       (id)         => api.get(`/tax-categories/${id}`).then(r => r.data),
  create:        (data)       => api.post('/tax-categories', data).then(r => r.data),
  update:        (id, data)   => api.put(`/tax-categories/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/tax-categories/${id}`),
  count:         ()           => api.get('/tax-categories/count').then(r => r.data),
};

export const taxRateApi = {
  getAll:        ()           => api.get('/tax-rates').then(r => r.data),
  getById:       (id)         => api.get(`/tax-rates/${id}`).then(r => r.data),
  create:        (data)       => api.post('/tax-rates', data).then(r => r.data),
  update:        (id, data)   => api.put(`/tax-rates/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/tax-rates/${id}`),
  count:         ()           => api.get('/tax-rates/count').then(r => r.data),
};

export const registerApi = {
  getAll:        ()           => api.get('/registers').then(r => r.data),
  getById:       (id)         => api.get(`/registers/${id}`).then(r => r.data),
  create:        (data)       => api.post('/registers', data).then(r => r.data),
  update:        (id, data)   => api.put(`/registers/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/registers/${id}`),
  count:         ()           => api.get('/registers/count').then(r => r.data),
  getByStore:    (storeId)    => api.get(`/registers/store/${storeId}`).then(r => r.data),
};

export const sessionApi = {
  getAll:        ()           => api.get('/sessions').then(r => r.data),
  getById:       (id)         => api.get(`/sessions/${id}`).then(r => r.data),
  create:        (data)       => api.post('/sessions', data).then(r => r.data),
  getByStatus:   (status)     => api.get(`/sessions/status/${status}`).then(r => r.data),
  getActiveByCashier: (id)    => api.get(`/sessions/cashier/${id}/active`).then(r => r.data),
  close:         (id, data)   => api.patch(`/sessions/${id}/close`, data).then(r => r.data),
};

export const saleApi = {
  getAll:        ()           => api.get('/sales').then(r => r.data),
  getById:       (id)         => api.get(`/sales/${id}`).then(r => r.data),
  create:        (data)       => api.post('/sales', data).then(r => r.data),
  complete:      (id, data)   => api.patch(`/sales/${id}/complete`, data).then(r => r.data),
  voidSale:      (id, data)   => api.patch(`/sales/${id}/void`, data).then(r => r.data),
  getBySession:  (sessionId)  => api.get(`/sales/session/${sessionId}`).then(r => r.data),
  count:         ()           => api.get('/sales/count').then(r => r.data),
};

export const saleLineItemApi = {
  getBySale:     (saleId)     => api.get(`/sale-line-items/sale/${saleId}`).then(r => r.data),
  create:        (data)       => api.post('/sale-line-items', data).then(r => r.data),
  update:        (id, data)   => api.put(`/sale-line-items/${id}`, data).then(r => r.data),
  delete:        (id)         => api.delete(`/sale-line-items/${id}`),
};
