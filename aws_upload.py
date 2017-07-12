import subprocess
from boto3.s3.transfer import S3Transfer
import boto3
import ids

filepath = "/Users/tyler/Desktop/awsupload"
zippath = "/Users/tyler/Desktop/awsupload.zip"

# Note: I am having trouble getting firebase_admin to install correct with pip.
# For now I have it installed in my working directory
# TODO I think boto is already installed on lambda
pip_to_install = ["python-firebase", "boto", "XlsxWriter", "inflect", "slacker", "pygithub", "joblib"]

subprocess.call(["rm", "-r", filepath])
subprocess.call(["mkdir", filepath])
subprocess.call(["cp", "-r", ".", filepath])

for item in pip_to_install:
    subprocess.call(["pip", "install", item, "-t", filepath])

subprocess.call(["zip", "-r", zippath, "."], cwd=filepath)
subprocess.call(["rm", "-r", filepath])

print("UPLOADING...")

client = boto3.client('s3', aws_access_key_id=ids.aws_access_key_id,aws_secret_access_key=ids.aws_secret_access_key)
transfer = S3Transfer(client)
transfer.upload_file(zippath, "tyler-aws-bucket", "awsupload.zip")
