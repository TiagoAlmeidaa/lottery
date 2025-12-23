import argparse
import sys

from google.oauth2 import service_account
import googleapiclient.discovery

TRACK = 'production'

argparse = argparse.ArgumentParser(add_help=False)
argparse.add_argument('package_name', help='The app package name. Example: com.github.example')
argparse.add_argument('apk_file', nargs='?', help='The path to the APK file to upload.')

def main(argv):
    flags = argparse.parse_args(args=argv[1:])

    print(argv)
    print("-> preparing to publish on: %s" % TRACK)

    credentials = service_account.Credentials.from_service_account_file(
        'google_play_android_publisher.json',
        scopes=['https://www.googleapis.com/auth/androidpublisher']
    )

    service = googleapiclient.discovery.build('androidpublisher', 'v3', credentials=credentials)

    print("-> androidpublisher API initialized")

    package_name = flags.package_name
    apk_file = flags.apk_file

    print("-> package: %s" % package_name)
    print("-> apk: %s" % apk_file)

    try:
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']

        print("-> app edit created")

        apk_response = service.edits().apks().upload(
            editId=edit_id,
            packageName=package_name,
            media_body=apk_file
        ).execute()

        print("-> apk with version code %d uploaded" % apk_response['versionCode'])

        track_response = service.edits().tracks().update(
            editId=edit_id,
            track=TRACK,
            packageName=package_name,
            body={'releases': [{
                'versionCodes': [str(apk_response['versionCode'])],
                'status': 'inProgress',
                'userFraction': 0.1
            }]}).execute()

        print("-> track: %s" % track_response['track'])
        print("-> releases: %s" % track_response['releases'])

        commit_request = service.edits().commit(
            editId=edit_id,
            packageName=package_name
        ).execute()

        print("-> changes has been commit (%s)" % commit_request['id'])

    except Exception as e:
        print(f'An error occurred: {e}')
        sys.exit(1)

if __name__ == '__main__':
    main(sys.argv)
