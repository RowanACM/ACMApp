import subprocess
from boto3.s3.transfer import S3Transfer
import boto3
import ids

filepath = "/Users/tyler/Desktop/awsupload"
zippath = "/Users/tyler/Desktop/awsupload.zip"

# TODO: Use the newest version of firebase-admin
# TODO I think boto is already installed on lambda
pip_to_install = ["python-firebase", "boto", "XlsxWriter", "inflect", "slacker", "pygithub", "joblib", "firebaseadmin==1.0.0"]

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

print("TRANSFERRING TO LAMBDA...")

lambda_client = boto3.client('lambda', aws_access_key_id=ids.aws_access_key_id,aws_secret_access_key=ids.aws_secret_access_key)

lambda_client.update_function_code(
    FunctionName=ids.lambda_id,
    #ZipFile=b'bytes',
    S3Bucket='tyler-aws-bucket',
    S3Key='awsupload.zip',
    Publish=True
)
