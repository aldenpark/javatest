import { useState } from "react";
import { useLeads } from "../hooks/useLeads";
import LeadsTable from "../components/LeadsTable";
import LeadForm from "../components/LeadForm";

export default function LeadsPage() {
  const {
    rows, total, page, size, setPage, setSize,
    loading, error, create, update, remove
  } = useLeads({ page: 0, size: 20, sort: "createdAt,desc" });

  const [editing, setEditing] = useState(null); // the lead being edited (or null)

  const onCreate = async (data) => {
    await create(data);
    // stays in create mode
  };

  const onStartEdit = (lead) => setEditing(lead);

  const onSaveEdit = async (patch) => {
    // patch may contain only changed fields; merge with the current one
    const payload = { ...editing, ...patch, id: editing.id };
    await update(editing.id, payload);
    setEditing(null);
  };

  const onCancelEdit = () => setEditing(null);

  const onQualify = (lead) => update(lead.id, { ...lead, status: "QUALIFIED" });
  const onDelete  = (lead) => remove(lead.id);

  return (
    <div className="p-4">
      <h1 className="text-xl font-semibold mb-4">Leads</h1>

      {editing ? (
        <LeadForm
          mode="edit"
          value={editing}
          onSubmit={onSaveEdit}
          onCancel={onCancelEdit}
          busy={loading}
        />
      ) : (
        <LeadForm
          mode="create"
          onSubmit={onCreate}
          busy={loading}
        />
      )}

      {error && <div className="error">{error}</div>}

      <LeadsTable
        rows={rows}
        onEdit={onStartEdit}      // <-- NEW: open edit mode
        onQualify={onQualify}
        onDelete={onDelete}
      />

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
