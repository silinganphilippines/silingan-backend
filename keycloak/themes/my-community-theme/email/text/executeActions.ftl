<#ftl output_format="plainText">
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

Hello,

Welcome to Silingan!

You have been invited to serve as the Community Admin for ${communityName}.

As a Community Admin, you'll be able to:
- Manage your community's profile and settings
- Create and publish announcements
- Manage staff accounts and permissions
- Maintain the community directory and emergency contacts
- Monitor and manage resident reports
- Oversee day-to-day community operations

To get started, please activate your account by clicking the link below:

${link}

For security reasons, this invitation link will expire within 24 hours. If it expires, please contact your Silingan administrator to request a new invitation.

If you weren't expecting this invitation, you may safely ignore this email.
We look forward to helping you build a more connected and responsive community.

Warm regards,
The Silingan Team



