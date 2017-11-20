import boto.ses
from email.mime.text import MIMEText
from email.mime.application import MIMEApplication
from email.mime.multipart import MIMEMultipart
import ids


class Email(object):
    def __init__(self, to, subject):
        self.to = to
        self.subject = subject
        self._html = None
        self._text = None
        self._attachmentFile = None
        self._attachmentName = None
        self._format = 'html'

    def html(self, html):
        self._html = html

    def text(self, text):
        self._text = text

    def attachment(self, attachmentFile, attachmentName):
        self._attachmentFile = attachmentFile
        self._attachmentName = attachmentName

    def send(self, from_addr=None):
        body = self._html

        if isinstance(self.to, basestring):
            self.to = [self.to]
        if not from_addr:
            from_addr = 'tyler@rowanacm.org'
        if not self._html and not self._text:
            raise Exception('You must provide a text or html body.')
        if not self._html:
            self._format = 'text'
            body = self._text

        msg = MIMEMultipart()

        # what a recipient sees if they don't use an email reader
        msg.preamble = 'Multipart message.\n'

        msg['Subject'] = self.subject
        msg['From'] = from_addr
        msg['To'] = self.to[0]
        msg.attach(MIMEText(self._text))

        # the attachment

        part = MIMEApplication(open(self._attachmentFile, 'rb').read())
        part.add_header('Content-Disposition', 'attachment', filename=self._attachmentName)
        msg.attach(part)

        connection = boto.ses.connect_to_region(
            'us-east-1',
            aws_access_key_id=ids.aws_access_key_id,
            aws_secret_access_key=ids.aws_secret_access_key
        )

        return connection.send_raw_email(msg.as_string()
                                           , source=msg['From']
                                           , destinations=[msg['To']])


        # return connection.send_email(
        #     from_addr,
        #     self.subject,
        #     None,
        #     self.to,
        #     format=self._format,
        #     text_body=self._text,
        #     html_body=self._html
        # )
