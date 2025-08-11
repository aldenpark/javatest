import { useState } from "react";

export default function LeadForm({ onSubmit, busy }) {
  const [form, setForm] = useState({ firstName: "", lastName: "" });
  const set = (k,v) => setForm(f => ({ ...f, [k]: v }));

  const submit = (e) => {
    e.preventDefault();
    onSubmit(form).then(() => setForm({ firstName: "", lastName: "" }));
  };

  return (
    <form onSubmit={submit} className="flex gap-2 mb-4">
      <input placeholder="First name" value={form.firstName} onChange={e => set("firstName", e.target.value)} />
      <input placeholder="Last name"  value={form.lastName}  onChange={e => set("lastName", e.target.value)} />
      <button type="submit" disabled={busy}>Add</button>
    </form>
  );
}
