import firebase
import xlsxwriter
import inflect
import Email
import ids

p = inflect.engine()

myfirebase = ids.myfirebase


def export_attendance():
    current = myfirebase.get('/attendance/status/current', None)
    signed_in_people = myfirebase.get('/attendance/' + current, None)

    to_write = []

    # Nobody signed in to the meeting. Quit without sending an email.
    if signed_in_people is None:
        return None

    for uid, details in signed_in_people.iteritems():
        detail_person = myfirebase.get("/members/" + uid, None)
        details["meeting_count"] = detail_person["meeting_count"]
        to_write.append(details)

    to_write = sorted(to_write, key=lambda k: k['email'])

    workbook = xlsxwriter.Workbook('/tmp/attendance.xlsx')
    worksheet = workbook.add_worksheet()

    # Add a format for the headings.
    bold = workbook.add_format({'bold': 1})

    # Create a format to use in the merged range.
    merge_format = workbook.add_format({
        'bold': 1,
        'border': 0,
        'align': 'center',
        'valign': 'vcenter',
        'fg_color': '#bababa'})


    title_list = current.split("_")
    title = title_list[0].title() + " " + p.ordinal(int(title_list[1]))

    # Merge 3 cells.
    worksheet.merge_range('A1:C1', 'Attendance ' + title, merge_format)
    worksheet.merge_range('A2:C2', 'Made by Rowan ACM acm@rowan.edu', merge_format)

    worksheet.write_row('A3', ["Name", "Email", "Meetings"], bold)

    names = []
    emails = []
    meeting_counts = []
    for item in to_write:
        names.append(item["name"])
        emails.append(item["email"])
        meeting_counts.append(item["meeting_count"])

    worksheet.write_column('A4', names)
    worksheet.write_column('B4', emails)
    worksheet.write_column('C4', meeting_counts)

    # Format width
    worksheet.set_column(0, 0, 20)
    worksheet.set_column(1, 1, 30)
    worksheet.set_column(2, 2, 10)

    workbook.close()

    email = Email.Email(to="carberryt9@students.rowan.edu", subject="ACM Attendance - " + title)
    email.text("Hello! Here is the attendance for " + title + ". If you have any questions, just reply to this email.\n\n- Tyler Carberry")

    # You can only write to /tmp on lambda
    email.attachment("/tmp/attendance.xlsx", "attendance_" + title_list[0] + "_" + title_list[1] + ".xlsx")

    email.send(from_addr="tyler@rowanacm.org")

    print("DONE")