import http from "../../lib/http";
export const fetchProfiles = () => http.get("/profiles").then(r => r.data);
