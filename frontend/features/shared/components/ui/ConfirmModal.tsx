"use client";

import { useEffect, useId, useRef, type ReactNode } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { FocusTrap } from "focus-trap-react";
import { AlertCircle, AlertTriangle, CheckCircle2, HelpCircle, Info, X } from "lucide-react";

export type ConfirmModalVariant = "confirm" | "success" | "error" | "warning" | "info";

export interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onYes?: () => void;
  onNo?: () => void;
  title: string;
  body: ReactNode;
  variant?: ConfirmModalVariant;
  yesButtonLabel?: string;
  noButtonLabel?: string;
  isLoading?: boolean;
  size?: "sm" | "md" | "lg";
  modalTestId?: string;
  confirmBtnTestId?: string;
  cancelBtnTestId?: string;
  closeBtnTestId?: string;
}

const sizes = { sm: "max-w-sm", md: "max-w-lg", lg: "max-w-2xl" };
const styles = {
  confirm: { Icon: HelpCircle, icon: "text-primary", bg: "bg-primary/10", button: "btn-primary" },
  success: { Icon: CheckCircle2, icon: "text-success", bg: "bg-success/10", button: "btn-success" },
  error: { Icon: AlertCircle, icon: "text-error", bg: "bg-error/10", button: "btn-error" },
  warning: { Icon: AlertTriangle, icon: "text-warning", bg: "bg-warning/10", button: "btn-warning" },
  info: { Icon: Info, icon: "text-info", bg: "bg-info/10", button: "btn-info" },
};

export default function ConfirmModal({
  isOpen,
  onClose,
  onYes,
  onNo,
  title,
  body,
  variant = "confirm",
  yesButtonLabel,
  noButtonLabel = "Cancel",
  isLoading = false,
  size = "md",
  modalTestId = "dlgConfirmPopup",
  confirmBtnTestId = "btnConfirm",
  cancelBtnTestId = "btnCancel",
  closeBtnTestId = "btnClose",
}: ConfirmModalProps) {
  const titleId = useId();
  const bodyId = useId();
  const dialogRef = useRef<HTMLDivElement>(null);
  const { Icon, icon, bg, button } = styles[variant];
  const hasActions = Boolean(yesButtonLabel);

  useEffect(() => {
    if (!isOpen) return;
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => { document.body.style.overflow = previousOverflow; };
  }, [isOpen]);

  const closeIfAllowed = () => {
    if (!isLoading) onClose();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <FocusTrap focusTrapOptions={{
          escapeDeactivates: false,
          clickOutsideDeactivates: false,
          fallbackFocus: () => dialogRef.current!,
        }}>
          <motion.div
            className="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 p-4 backdrop-blur-[2px]"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.18 }}
            onMouseDown={(event) => {
              if (event.target === event.currentTarget) closeIfAllowed();
            }}
            onKeyDown={(event) => {
              if (event.key === "Escape") {
                event.stopPropagation();
                closeIfAllowed();
              }
            }}
          >
            <motion.div
              ref={dialogRef}
              tabIndex={-1}
              role="dialog"
              aria-modal="true"
              aria-labelledby={titleId}
              aria-describedby={bodyId}
              data-testid={modalTestId}
              initial={{ opacity: 0, scale: 0.96, y: 8 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.96, y: 8 }}
              transition={{ duration: 0.2 }}
              className={`w-full ${sizes[size]} overflow-hidden rounded-2xl border border-base-300 bg-base-100 shadow-2xl`}
            >
              <div className="flex items-start gap-4 p-6 pb-4">
                <div className={`flex size-12 shrink-0 items-center justify-center rounded-full ${bg}`}>
                  <Icon size={24} className={icon} aria-hidden="true" />
                </div>
                <div className="min-w-0 flex-1 pt-1">
                  <h2 id={titleId} className="text-lg font-bold text-base-content">{title}</h2>
                </div>
                <button
                  type="button"
                  data-testid={closeBtnTestId}
                  className="btn btn-ghost btn-square btn-sm"
                  onClick={closeIfAllowed}
                  disabled={isLoading}
                  aria-label="Close modal"
                >
                  <X size={18} />
                </button>
              </div>

              <div id={bodyId} className="max-h-[55vh] overflow-y-auto px-6 pb-6 text-sm leading-relaxed text-base-content/75 sm:text-base">
                {typeof body === "string" ? <p>{body}</p> : body}
              </div>

              <div className="flex flex-wrap justify-end gap-2 border-t border-base-300 bg-base-200/30 px-6 py-4">
                {hasActions ? (
                  <>
                    <button
                      type="button"
                      data-testid={cancelBtnTestId}
                      className="btn btn-ghost"
                      onClick={() => { if (onNo) onNo(); else onClose(); }}
                      disabled={isLoading}
                    >
                      {noButtonLabel}
                    </button>
                    <button
                      type="button"
                      data-testid={confirmBtnTestId}
                      className={`btn ${button}`}
                      onClick={onYes ?? onClose}
                      disabled={isLoading}
                    >
                      {isLoading && <span className="loading loading-spinner loading-xs" />}
                      {yesButtonLabel}
                    </button>
                  </>
                ) : (
                  <button type="button" data-testid={confirmBtnTestId} className={`btn ${button}`} onClick={closeIfAllowed} disabled={isLoading}>
                    OK
                  </button>
                )}
              </div>
            </motion.div>
          </motion.div>
        </FocusTrap>
      )}
    </AnimatePresence>
  );
}
