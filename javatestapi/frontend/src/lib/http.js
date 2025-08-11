import axios from "axios";
import { API_BASE_URL } from "./config";

const http = axios.create({ baseURL: API_BASE_URL });

// Central place for auth headers, error logging, retries, etc.
http.interceptors.response.use(
  res => res,
  err => {
    // TODO: route 401 to login, surface messages, etc.
    console.error("API error:", err?.response || err);
    return Promise.reject(err);
  }
);

export default http;
