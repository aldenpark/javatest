import http from "../../lib/http";

// Spring Data Page<T>
/*
type PageResp<T> = {
  content: T[], totalElements: number, totalPages: number,
  number: number, size: number, first: boolean, last: boolean
}
*/

export const listLeads = (page=0, size=20, sort="createdAt,desc") =>
  http.get("/leads", { params: { page, size, sort } }).then(r => r.data);

export const getLead = id => http.get(`/leads/${id}`).then(r => r.data);
export const createLead = lead => http.post("/leads", lead).then(r => r.data);
export const updateLead = (id, lead) => http.put(`/leads/${id}`, lead).then(r => r.data);
export const deleteLead = id => http.delete(`/leads/${id}`).then(r => r.data);
