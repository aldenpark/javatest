import { useCallback, useEffect, useState } from "react";
import { listLeads, createLead, updateLead, deleteLead } from "../api";

export function useLeads(initial = { page: 0, size: 20, sort: "createdAt,desc" }) {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(initial.page);
  const [size, setSize] = useState(initial.size);
  const [sort, setSort] = useState(initial.sort);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true); setError(null);
    try {
      const data = await listLeads(page, size, sort);
      setRows(data.content); setTotal(data.totalElements);
    } catch (e) {
      setError(e?.message || "Failed to load leads");
    } finally {
      setLoading(false);
    }
  }, [page, size, sort]);

  useEffect(() => { load(); }, [load]);

  const create = async (lead) => { await createLead(lead); await load(); };
  const update = async (id, lead) => { await updateLead(id, lead); await load(); };
  const remove = async (id) => { await deleteLead(id); await load(); };

  return { rows, total, page, size, sort, setPage, setSize, setSort, loading, error, create, update, remove, reload: load };
}
