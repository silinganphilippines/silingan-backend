import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Silingan Community Portal",
  description: "Community RBAC Management System",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="antialiased">{children}</body>
    </html>
  );
}
