import { useLeads } from "../hooks/useLeads";
import LeadsTable from "../components/LeadsTable";
import LeadForm from "../components/LeadForm";

export default function LeadsPage() {
  const { rows, total, page, size, setPage, setSize, loading, error, create, update, remove } =
    useLeads({ page: 0, size: 20, sort: "createdAt,desc" });

  const onQualify = (lead) => update(lead.id, { ...lead, status: "Qualified" });
  const onDelete  = (lead) => remove(lead.id);

  return (
    <div className="p-4">
      <h1 className="text-xl font-semibold mb-4">Leads</h1>

      <LeadForm onSubmit={create} busy={loading} />

      {error && <div className="error">{error}</div>}
      <LeadsTable rows={rows} onQualify={onQualify} onDelete={onDelete} />

      {/* Pagination */}
      <div className="mt-3 flex items-center gap-2">
        <button onClick={() => setPage(Math.max(0, page - 1))} disabled={page === 0 || loading}>Prev</button>
        <span>Page {page + 1}</span>
        <button onClick={() => setPage(page + 1)} disabled={(page + 1) * size >= total || loading}>Next</button>
        <select value={size} onChange={e => setSize(parseInt(e.target.value, 10))}>
          {[10,20,50].map(s => <option key={s} value={s}>{s}/page</option>)}
        </select>
      </div>
    </div>
  );
}
