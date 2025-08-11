export default function LeadsTable({ rows, onQualify, onDelete }) {
  return (
    <table width="100%" cellPadding={8} border={1}>
      <thead>
        <tr>
          <th>Name</th><th>Email</th><th>Company</th><th>Status</th><th>Active</th><th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {rows.map(r => (
          <tr key={r.id}>
            <td>{r.firstName} {r.lastName}</td>
            <td>{r.email || "-"}</td>
            <td>{r.company || "-"}</td>
            <td>{r.status || "-"}</td>
            <td>{r.active ? "Yes" : "No"}</td>
            <td>
              <button onClick={() => onQualify(r)}>Qualify</button>
              <button onClick={() => onDelete(r)}>Delete</button>
            </td>
          </tr>
        ))}
        {!rows.length && <tr><td colSpan={6}>No leads</td></tr>}
      </tbody>
    </table>
  );
}
