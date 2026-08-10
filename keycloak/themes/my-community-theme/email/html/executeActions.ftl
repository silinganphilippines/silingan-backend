<#ftl output_format="HTML">
<#import "template.ftl" as layout>

<#assign communityName = "your community">
<#if user?? && user.attributes?? && user.attributes.communityName??>
  <#if user.attributes.communityName?is_sequence>
    <#if user.attributes.communityName?size gt 0>
      <#assign communityName = user.attributes.communityName[0]>
    </#if>
  <#else>
    <#assign communityName = user.attributes.communityName>
  </#if>
</#if>

<@layout.emailLayout>
<p>Hello,</p>

<p>Welcome to Silingan!</p>

<p>
  You have been invited to serve as the <strong>Community Admin</strong> for
  <strong>${kcSanitize(communityName)?no_esc}</strong>.
</p>

<p>As a Community Admin, you'll be able to:</p>
<ul>
  <li>Manage your community's profile and settings</li>
  <li>Create and publish announcements</li>
  <li>Manage staff accounts and permissions</li>
  <li>Maintain the community directory and emergency contacts</li>
  <li>Monitor and manage resident reports</li>
  <li>Oversee day-to-day community operations</li>
</ul>

<p>To get started, please activate your account by clicking the button below.</p>

<p>
  <a href="${link}" style="display:inline-block;padding:12px 20px;background:#0a2e11;color:#ffffff;text-decoration:none;border-radius:6px;font-weight:600" rel="nofollow">
    Activate My Account
  </a>
</p>

<p>
  For security reasons, this invitation link will expire within
  <strong>24 hours</strong>. If it expires,
  please contact your Silingan administrator to request a new invitation.
</p>

<p>
  If you weren't expecting this invitation, you may safely ignore this email.<br/>
  We look forward to helping you build a more connected and responsive community.
</p>

<p>
  Warm regards,<br/>
  <strong>The Silingan Team</strong>
</p>
</@layout.emailLayout>



