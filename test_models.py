import os
import site
import face_recognition
import face_recognition_models
import numpy as np

# Print all site-packages directories
print("All site-packages directories:")
for path in site.getsitepackages():
    print(f"- {path}")

# Try to find the actual models directory
base_path = os.path.dirname(face_recognition_models.__file__)
models_path = os.path.join(base_path, 'models')
print(f"\nActual models directory:")
print(f"Base path: {base_path}")
print(f"Models path: {models_path}")
print(f"Directory exists: {os.path.exists(models_path)}")

if os.path.exists(models_path):
    print(f"Directory contents: {os.listdir(models_path)}")

# Test if face recognition is working
print("\nTesting face recognition functionality:")
try:
    # Create a small test image (black square)
    test_image = np.zeros((100, 100, 3), dtype=np.uint8)
    print("- Created test image")
    
    # Try to use face_recognition functionality
    face_locations = face_recognition.face_locations(test_image)
    print("- Face detection is working!")
    print(f"- Found {len(face_locations)} faces in test image (should be 0)")
    
except Exception as e:
    print(f"Error during face recognition test: {str(e)}")

# Try to directly access predictor path
try:
    predictor_path = face_recognition.api._face_detector
    print(f"\nFace detector path: {predictor_path}")
    print(f"Face detector exists: {os.path.exists(predictor_path)}")
except Exception as e:
    print(f"Error accessing face detector path: {str(e)}")