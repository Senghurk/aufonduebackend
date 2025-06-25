# Azure Blob Storage Token Renewal Guide for AU Fondue Backend

## Overview

This guide provides step-by-step instructions for renewing the Azure Blob Storage SAS (Shared Access Signature) token used by the AU Fondue backend application for file storage operations.

## Background

The AU Fondue backend uses Azure Blob Storage to store uploaded photos from maintenance reports. Access to the blob storage is controlled through SAS tokens, which have expiration dates and need to be renewed periodically to maintain application functionality.

## Current Configuration

Based on the codebase analysis, the current SAS token configuration is:

```
Current Token: sp=racwl&st=2025-02-25T05:25:29Z&se=2025-05-01T13:25:29Z&sv=2022-11-02&sr=c&sig=tLy7KN14KKIju3BkWj3j9WxhTRlFYJZEh404Wfp9lTQ%3D
Expiration Date: May 1, 2025, 13:25:29 UTC
Storage Container: aufondue
Blob Storage URL: https://aufondueblob.blob.core.windows.net/aufondue/
```

## When to Renew

- **Proactive Renewal**: 1-2 weeks before the expiration date
- **Emergency Renewal**: When the application starts failing to upload/access images
- **Scheduled Renewal**: Set up calendar reminders for token expiration dates

## Step-by-Step Renewal Process

### 1. Access Azure Portal

1. Navigate to [Azure Portal](https://portal.azure.com)
2. Sign in with your Azure account credentials
3. Locate the **aufondueblob** storage account

### 2. Navigate to Storage Account

1. In the Azure Portal search bar, type "aufondueblob"
2. Click on the storage account from the search results
3. Alternatively, go to **Storage accounts** → **aufondueblob**

### 3. Generate New SAS Token

1. In the storage account menu, find **Security + networking** section
2. Click on **Shared access signature**
3. Configure the SAS token settings:

#### Allowed Services

- ✅ **Blob** (checked)
- ❌ File (unchecked)
- ❌ Queue (unchecked)
- ❌ Table (unchecked)

#### Allowed Resource Types

- ❌ Service (unchecked)
- ✅ **Container** (checked)
- ✅ **Object** (checked)

#### Allowed Permissions

- ✅ **Read** (r)
- ✅ **Add** (a)
- ✅ **Create** (c)
- ✅ **Write** (w)
- ✅ **List** (l)
- ❌ Delete (unchecked for security)
- ❌ Process (unchecked)
- ❌ Filter (unchecked)

#### Time Settings

- **Start time**: Current date/time (or slightly in the past)
- **Expiry time**: Set 3-6 months in the future
- **Time zone**: UTC

#### Protocol

- Select **HTTPS only**

### 4. Generate and Copy Token

1. Click **Generate SAS and connection string**
2. Copy the **SAS token** (the part after the `?`)
3. Save this token securely for the next steps

### 5. Update Backend Configuration

The SAS token needs to be updated in multiple locations:

#### Backend Application Properties

Update the file: `src/main/resources/application.properties`

```properties
# Update the SAS token in the Azure Blob Storage configuration
azure.blob-storage.sas-token=NEW_SAS_TOKEN_HERE
```

#### RetrofitClient.kt (Mobile App)

Update the file: `app/src/main/java/edu/au/aufondue/api/RetrofitClient.kt`

```kotlin
// Update the SAS_TOKEN constant
private const val SAS_TOKEN = "NEW_SAS_TOKEN_HERE"
```

#### Admin Web Application

Update the files that reference the SAS token:

**File**: `app/assignedReports/page.js`

```javascript
const sastoken = "?NEW_SAS_TOKEN_HERE";
```

**File**: `app/reports/page.js`

```javascript
const sastoken = "?NEW_SAS_TOKEN_HERE";
```

### 6. Test the Configuration

#### Backend Testing

1. Deploy the updated backend application
2. Test image upload functionality through the API
3. Verify images can be accessed via the generated URLs

#### Mobile App Testing

1. Build and deploy the updated mobile application
2. Test photo upload in the report submission flow
3. Verify images display correctly in the app

#### Admin Panel Testing

1. Deploy the updated web application
2. Test image display in the admin dashboard
3. Verify all report photos load correctly

## Security Best Practices

### Token Permissions

- Only grant minimum required permissions (read, add, create, write, list)
- Avoid granting delete permissions unless absolutely necessary
- Use HTTPS-only protocol

### Token Storage

- Store tokens as environment variables in production
- Never commit tokens to version control
- Use Azure Key Vault for secure token storage in production

### Monitoring

- Set up Azure Monitor alerts for token expiration
- Monitor blob storage access logs for unauthorized access
- Implement application logging for token-related errors

## Automation Opportunities

### Automated Renewal Script

Consider implementing an automated renewal process:

```bash
#!/bin/bash
# Script to automate SAS token renewal
# This would use Azure CLI to generate new tokens programmatically

# Login to Azure
az login

# Generate new SAS token
NEW_TOKEN=$(az storage container generate-sas \
  --name aufondue \
  --account-name aufondueblob \
  --permissions racwl \
  --expiry 2025-08-01T00:00:00Z \
  --https-only \
  --output tsv)

echo "New SAS Token: $NEW_TOKEN"
```

### CI/CD Integration

- Add token renewal to your deployment pipeline
- Use Azure DevOps or GitHub Actions to automate updates
- Implement blue-green deployment for zero-downtime token updates

## Troubleshooting

### Common Issues

**Images not loading in mobile app:**

- Check if SAS token has expired
- Verify token has correct permissions (read, list)
- Ensure HTTPS protocol is enforced

**Upload failures in backend:**

- Verify token has write and create permissions
- Check if container name is correct in the URL
- Ensure token hasn't expired

**403 Forbidden errors:**

- Token may have expired
- Check if IP restrictions are applied
- Verify token permissions include required operations

### Error Messages to Watch For

```
HTTP 403: Forbidden - Token may be expired or invalid
HTTP 404: Not Found - Container or blob name may be incorrect
HTTP 400: Bad Request - Token format may be malformed
```

## Contact Information

For issues with token renewal or Azure access:

- **Development Team**: [Your team contact]
- **Azure Administrator**: [Azure admin contact]
- **Emergency Contact**: [Emergency contact for production issues]

## Checklist for Token Renewal

- [ ] Current token expiration date identified
- [ ] New SAS token generated with correct permissions
- [ ] Backend application.properties updated
- [ ] Mobile app RetrofitClient.kt updated
- [ ] Admin panel JavaScript files updated
- [ ] Backend application redeployed
- [ ] Mobile app rebuilt and deployed
- [ ] Admin panel redeployed
- [ ] Image upload functionality tested
- [ ] Image display functionality tested
- [ ] New expiration date added to calendar
- [ ] Documentation updated with new token details

## Next Renewal Date

**Current Token Expires**: May 1, 2025, 13:25:29 UTC
**Recommended Renewal Date**: April 17, 2025
**Set Calendar Reminder**: April 10, 2025

---

*Last Updated: [Current Date]*
*Next Review: [Token Expiration - 2 weeks]*
