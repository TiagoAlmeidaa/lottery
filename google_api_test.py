import argparse
import sys

from google.oauth2 import service_account
import googleapiclient.discovery

argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('package_name',
                       help='The package name. Example: com.android.sample')

def main(argv):
    flags = argparser.parse_args(args=argv[1:])

    print()
    print("google_api_test.py Starting")

    credentials = service_account.Credentials.from_service_account_file(
        'google_play_android_publisher.json',
        scopes=['https://www.googleapis.com/auth/androidpublisher']
    )

    service = googleapiclient.discovery.build('androidpublisher', 'v3', credentials=credentials)

    package_name = flags.package_name

    try:
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']

        tracks_result = service.edits().tracks().list(
            packageName=package_name,
            editId=edit_id
        ).execute()

        tracks = tracks_result['tracks']
        for track in tracks:
            print()
            print("[INFORMATION] Track: %s" % track['track'])
            try:
                releases = track['releases']
                for release in releases:
                    print("[INFORMATION] Release: %s" % release)
            except Exception as e:
                print("[INFORMATION] Release not found")

    except Exception as e:
        print(f'An error occurred: {e}')
    finally:
        print()
        print("google_api_test.py Ending")
        print()

if __name__ == '__main__':
    main(sys.argv)
