import React from "react";
export default class ErrorBoundary extends React.Component {
  state = { hasError: false, error: null };
  static getDerivedStateFromError(error) { return { hasError: true, error }; }
  componentDidCatch(error, info) { /* TODO: log to service */ }
  render() {
    if (this.state.hasError) return <div className="error">Something went wrong.</div>;
    return this.props.children;
  }
}
