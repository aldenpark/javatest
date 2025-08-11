import { useEffect, useState } from "react";

export default function LeadForm({ value, onSubmit, onCancel, busy, mode = "create" }) {
  const [form, setForm] = useState({ firstName: "", lastName: "", email: "", company: "" });
  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  useEffect(() => {
    if (value) setForm({
      firstName: value.firstName || "",
      lastName:  value.lastName  || "",
      email:     value.email     || "",
      company:   value.company   || ""
    });
  }, [value]);

  const submit = async (e) => {
    e.preventDefault();
    await onSubmit(form);
    if (mode === "create") setForm({ firstName: "", lastName: "", email: "", company: "" });
  };

  return (
    <form onSubmit={submit} className="flex gap-2 mb-4">
      <input placeholder="First name" value={form.firstName} onChange={e => set("firstName", e.target.value)} />
      <input placeholder="Last name"  value={form.lastName}  onChange={e => set("lastName",  e.target.value)} />
      <input placeholder="Email"      value={form.email}     onChange={e => set("email",     e.target.value)} />
      <input placeholder="Company"    value={form.company}   onChange={e => set("company",   e.target.value)} />
      <button type="submit" disabled={busy}>{mode === "edit" ? "Save" : "Add"}</button>
      {mode === "edit" && <button type="button" onClick={onCancel} disabled={busy}>Cancel</button>}
    </form>
  );
}
