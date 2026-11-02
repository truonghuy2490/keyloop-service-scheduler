"use client";

import { Search, X } from "lucide-react";

interface SearchProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

export default function SearchInput({
  value,
  onChange,
  placeholder = "Search...",
}: SearchProps) {
  return (
    <div className="relative w-full max-w-md">
      <Search
        size={18}
        className="absolute left-3 top-1/2 -translate-y-1/2 text-base-content/50"
      />

      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="input input-bordered w-full pl-10 pr-10"
      />

      {value && (
        <button
          type="button"
          onClick={() => onChange("")}
          className="btn btn-ghost btn-xs absolute right-2 top-1/2 -translate-y-1/2"
          aria-label="Clear search"
        >
          <X size={16} />
        </button>
      )}
    </div>
  );
}