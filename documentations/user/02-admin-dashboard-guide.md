# AU Fondue Admin Dashboard - Administrator Guide

## Introduction

Welcome to the AU Fondue Admin Dashboard. This comprehensive guide will help you manage campus facility issues, oversee staff operations, and maintain the system efficiently. As an administrator, you have full control over the AU Fondue system.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Dashboard Overview](#dashboard-overview)
3. [Issue Management](#issue-management)
4. [User Management](#user-management)
5. [Staff Assignment](#staff-assignment)
6. [Reports and Analytics](#reports-and-analytics)
7. [System Settings](#system-settings)
8. [Advanced Features](#advanced-features)
9. [Best Practices](#best-practices)

---

## Getting Started

### System Requirements
- **Browser**: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+
- **Screen Resolution**: Minimum 1366x768 (1920x1080 recommended)
- **Internet**: Stable broadband connection
- **JavaScript**: Must be enabled

### Accessing the Admin Dashboard
1. Navigate to: `https://aufondue-admin.au.edu`
2. Enter your administrator credentials
3. Complete two-factor authentication
4. You'll be directed to the main dashboard

### First Time Login
1. You'll receive credentials via secure email
2. Login with temporary password
3. Set up new strong password
4. Configure two-factor authentication
5. Complete profile setup

---

## Dashboard Overview

### Main Dashboard Components

#### Top Navigation Bar
- **Logo**: Click to return to dashboard
- **Search**: Global search for issues, users
- **Notifications**: System alerts and updates
- **Profile**: Account settings and logout

#### Sidebar Menu
- **Dashboard**: Main overview screen
- **Issues**: Complete issue management
- **Users**: User and staff management
- **Reports**: Analytics and reporting
- **Settings**: System configuration
- **Help**: Documentation and support

#### Dashboard Widgets

##### Key Metrics (Top Row)
- **Total Open Issues**: Current active issues
- **Today's Reports**: New issues reported today
- **Resolution Rate**: Percentage resolved this month
- **Average Resolution Time**: In hours/days

##### Real-time Activity Feed
- Latest reported issues
- Recent status changes
- Staff assignments
- System notifications

##### Charts and Graphs
- **Issue Trends**: 30-day issue volume graph
- **Category Distribution**: Pie chart of issue types
- **Staff Performance**: Resolution metrics by staff
- **Priority Breakdown**: Issues by priority level

---

## Issue Management

### Viewing Issues

#### Issue List View
Access via sidebar: Issues → All Issues

**Available Filters:**
- Status (Pending, In Progress, Resolved, Closed)
- Priority (High, Medium, Low)
- Category (Maintenance, Cleaning, Security, IT, Other)
- Date Range
- Assigned Staff
- Building/Location
- Reporter Type (Student/Staff)

**List Columns:**
- ID (Click to view details)
- Title
- Reporter
- Category
- Priority (Color-coded)
- Status
- Assigned To
- Created Date
- Last Updated
- Actions

#### Issue Detail View
Click any issue ID or title to open detailed view:

**Information Displayed:**
- Complete description
- Reporter information and contact
- Exact location with map
- Attached photos/videos
- Full timeline of events
- Comments and internal notes
- Status history
- Resolution details

### Managing Issues

#### Changing Issue Status
1. Open issue details
2. Click "Change Status" button
3. Select new status:
   - **Under Review**: Initial assessment
   - **Assigned**: Delegated to staff
   - **In Progress**: Work has begun
   - **Pending Info**: Need more details
   - **On Hold**: Temporarily paused
   - **Resolved**: Issue fixed
   - **Closed**: Finalized
   - **Cancelled**: Won't fix/duplicate
4. Add status note (required)
5. Click "Update Status"

#### Assigning Issues to Staff
1. Open issue details
2. Click "Assign Staff" button
3. Select staff member from dropdown
   - Shows availability status
   - Current workload indicator
   - Expertise tags
4. Add assignment note
5. Set priority for staff
6. Click "Assign"
7. Staff receives notification

#### Adding Admin Notes
- **Internal Notes**: Visible only to staff/admins
- **Public Comments**: Visible to reporter
- **System Messages**: Automated updates

To add notes:
1. Scroll to Comments section
2. Choose note type
3. Enter message
4. Attach files if needed
5. Click "Add Note"

#### Bulk Actions
Select multiple issues using checkboxes:
- **Bulk Assign**: Assign to one staff member
- **Bulk Status Update**: Change status for all
- **Bulk Priority Change**: Update priority
- **Export Selected**: Download as Excel/PDF
- **Bulk Close**: Close resolved issues

### Issue Escalation

#### Escalating High Priority Issues
1. Open issue with concerning content
2. Click "Escalate" button
3. Select escalation level:
   - Department Head
   - Facility Manager
   - Emergency Response
4. Add escalation reason
5. System sends immediate notifications

#### Emergency Procedures
For safety-critical issues:
1. Mark as "Emergency" priority
2. System auto-notifies:
   - Security team
   - Facility manager
   - Department heads
3. Follow up with phone call
4. Document actions taken

---

## User Management

### User Roles and Permissions

#### Role Hierarchy
1. **Super Admin**: Full system access
2. **Admin**: Department-level control
3. **Supervisor**: Team management
4. **Staff**: Issue handling
5. **Viewer**: Read-only access

### Managing User Accounts

#### Creating New Users
1. Navigate to Users → Add New User
2. Fill in required information:
   - Email address
   - Full name
   - Department
   - Role selection
   - Phone number
3. Set permissions:
   - Areas of access
   - Feature restrictions
   - Data visibility
4. Click "Create User"
5. System sends welcome email

#### Editing User Profiles
1. Go to Users → All Users
2. Search or browse for user
3. Click "Edit" button
4. Modify information:
   - Personal details
   - Role and permissions
   - Department assignment
   - Active/Inactive status
5. Save changes

#### Deactivating Users
1. Find user in list
2. Click "Actions" → "Deactivate"
3. Choose deactivation type:
   - **Temporary**: Vacation/Leave
   - **Permanent**: Termination
4. Reassign open issues
5. Confirm deactivation

### Staff Performance Management

#### Viewing Staff Metrics
Navigate to Users → Staff Performance

**Metrics Displayed:**
- Issues resolved (daily/weekly/monthly)
- Average resolution time
- Customer satisfaction rating
- Response time
- Active issues count
- Overdue issues

#### Performance Reports
1. Select staff member
2. Choose date range
3. Generate report showing:
   - Productivity trends
   - Issue categories handled
   - Time management
   - Feedback received
4. Export as PDF for reviews

---

## Staff Assignment

### Assignment Strategies

#### Manual Assignment
Best for complex or high-priority issues:
1. Review issue details
2. Consider staff expertise
3. Check current workload
4. Make assignment
5. Add instructions

#### Auto-Assignment Rules
Configure at Settings → Auto-Assignment:
1. **Round-Robin**: Distribute equally
2. **Load-Based**: Assign to least busy
3. **Skill-Based**: Match expertise
4. **Location-Based**: By building/area
5. **Priority-Based**: Senior staff for high priority

### Managing Workload

#### Workload Dashboard
View at Staff → Workload Overview:
- Visual workload chart
- Issues per staff member
- Estimated completion times
- Overdue issue warnings
- Leave calendar integration

#### Rebalancing Assignments
1. Identify overloaded staff
2. Select issues to reassign
3. Choose new assignee
4. Notify both parties
5. Update timelines

### Shift Management

#### Setting Staff Schedules
1. Go to Staff → Schedules
2. Select staff member
3. Define work hours:
   - Regular shifts
   - On-call periods
   - Vacation/leave
4. System auto-adjusts assignments

---

## Reports and Analytics

### Standard Reports

#### Daily Operations Report
Automatically generated each morning:
- Previous day's issues
- Resolution statistics
- Outstanding priorities
- Staff performance
- System health

#### Weekly Summary Report
Generated every Monday:
- Week-over-week trends
- Category analysis
- Average resolution times
- User satisfaction scores
- Upcoming maintenance

#### Monthly Analytics Report
Comprehensive monthly analysis:
- Issue volume trends
- Performance metrics
- Cost analysis
- Predictive insights
- Recommendations

### Custom Reports

#### Creating Custom Reports
1. Navigate to Reports → Custom Report Builder
2. Select data sources:
   - Issues database
   - User data
   - Performance metrics
3. Choose fields to include
4. Apply filters and date ranges
5. Select visualization type:
   - Tables
   - Charts
   - Graphs
   - Heatmaps
6. Save report template
7. Schedule automatic generation

#### Exporting Reports
Available formats:
- **PDF**: For presentations
- **Excel**: For detailed analysis
- **CSV**: For data processing
- **PowerBI**: For advanced analytics

### Key Performance Indicators (KPIs)

#### System KPIs
- First Response Time
- Mean Time to Resolution
- Issue Recurrence Rate
- User Satisfaction Score
- System Uptime
- Staff Utilization Rate

#### Setting KPI Targets
1. Go to Reports → KPI Management
2. Select metric
3. Set target values
4. Configure alerts for deviations
5. Track progress on dashboard

---

## System Settings

### General Settings

#### Organization Information
- University name and logo
- Contact information
- Operating hours
- Holiday calendar
- Emergency contacts

#### System Preferences
- Default language
- Date/time format
- Currency symbol
- Measurement units
- Timezone settings

### Notification Settings

#### Email Templates
Customize automated emails:
1. Go to Settings → Email Templates
2. Select template type:
   - Issue reported
   - Status changed
   - Assignment notification
   - Resolution confirmation
3. Edit content with variables
4. Preview and test
5. Save changes

#### Notification Rules
Configure when notifications are sent:
- Immediate for high priority
- Batched for low priority
- Escalation timers
- Reminder schedules

### Category Management

#### Adding New Categories
1. Navigate to Settings → Categories
2. Click "Add Category"
3. Enter:
   - Category name
   - Description
   - Icon selection
   - Default priority
   - Auto-assignment rules
4. Save category

#### Modifying Categories
- Edit names and descriptions
- Change icons and colors
- Update assignment rules
- Set active/inactive status
- Merge categories

### Location Management

#### Building/Area Setup
1. Go to Settings → Locations
2. Add buildings:
   - Building name
   - Number of floors
   - Room/area list
   - Facility manager
3. Create location hierarchy
4. Upload floor plans (optional)

### Integration Settings

#### API Configuration
- Generate API keys
- Set rate limits
- Configure webhooks
- Manage external integrations

#### Single Sign-On (SSO)
- Microsoft Azure AD settings
- LDAP configuration
- OAuth providers
- Session management

---

## Advanced Features

### Automation

#### Creating Automation Rules
1. Navigate to Settings → Automation
2. Click "New Rule"
3. Define trigger:
   - Issue created
   - Status changed
   - Time-based
   - Threshold reached
4. Set conditions
5. Define actions:
   - Auto-assign
   - Send notification
   - Change status
   - Create task
6. Test and activate

#### Workflow Templates
Pre-built workflows for common scenarios:
- Emergency response
- Routine maintenance
- IT support tickets
- Cleaning requests

### Data Management

#### Data Backup
- Automatic daily backups
- Manual backup option
- Restore procedures
- Backup verification

#### Data Export
1. Go to Settings → Data Export
2. Select data type
3. Choose date range
4. Select format
5. Download or email

#### Data Retention
Configure at Settings → Data Retention:
- Issue retention period
- User data retention
- File storage limits
- Automatic purging rules

### System Maintenance

#### Health Monitoring
Dashboard shows:
- System performance
- Database status
- Storage usage
- API response times
- Error rates

#### Scheduled Maintenance
1. Plan maintenance window
2. Create announcement
3. Set system to maintenance mode
4. Perform updates
5. Test functionality
6. Restore normal operation

---

## Best Practices

### Daily Tasks
✅ Review overnight issues (Morning)
✅ Check staff attendance
✅ Monitor high-priority issues
✅ Respond to escalations
✅ Review performance dashboard
✅ Clear pending approvals

### Weekly Tasks
✅ Review weekly report
✅ Staff performance check
✅ Update issue categories
✅ Clean up closed issues
✅ Plan next week's priorities
✅ Team meeting preparation

### Monthly Tasks
✅ Generate monthly reports
✅ Review KPI achievements
✅ Update user permissions
✅ System health check
✅ Backup verification
✅ Policy review and updates

### Issue Handling Best Practices
1. **Respond Quickly**: Acknowledge within 2 hours
2. **Accurate Categorization**: Ensures proper routing
3. **Clear Communication**: Update reporters regularly
4. **Document Everything**: Maintain detailed logs
5. **Follow Up**: Verify resolution satisfaction
6. **Learn from Patterns**: Identify recurring issues

### Security Best Practices
1. **Strong Passwords**: Enforce complexity requirements
2. **Regular Audits**: Review user access monthly
3. **Data Protection**: Encrypt sensitive information
4. **Access Control**: Principle of least privilege
5. **Activity Monitoring**: Review logs regularly
6. **Incident Response**: Have plan ready

---

## Troubleshooting

### Common Issues

#### Cannot Login
- Verify credentials
- Check account status
- Clear browser cache
- Disable VPN
- Contact IT support

#### Reports Not Loading
- Check date range selection
- Verify data availability
- Clear browser cache
- Try different browser
- Check internet speed

#### Email Notifications Not Sending
- Verify SMTP settings
- Check email templates
- Review spam filters
- Confirm recipient addresses
- Check server logs

#### Performance Issues
- Clear browser cache
- Check internet connection
- Reduce concurrent users
- Archive old data
- Contact system admin

### Getting Help
- **Documentation**: Built-in help system
- **Support Ticket**: Create from Help menu
- **Email**: admin-support@au.edu
- **Phone**: Extension 1234
- **Training**: Schedule via HR portal

---

*Version 1.0 - Administrator Guide*
*Last updated: December 2024*
*© 2024 Assumption University*